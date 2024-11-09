package com.utility.util

import kamon.Kamon
import kamon.Kamon.runWithContextEntry
import kamon.context.Storage
import kamon.trace.Span
import kamon.util.CallingThreadExecutionContext

import scala.concurrent.{ExecutionContext, Future}

object KamonTrace {

  def markSpan[A](operationName: String, extraTags: Map[String, String] = Map.empty)(
    future: => Future[A]
  )(implicit ec: ExecutionContext): Future[A] = {
    Future {
      val builder = Kamon.spanBuilder(operationName)
      extraTags.foreach {
        case (key, value) =>
          builder.tag(key, value)
      }
      val newSpan = builder.start()
      val scope = Kamon.storeContext(Kamon.currentContext().withEntry(Span.Key, newSpan))

      try future.transform(
        res => {
          finishSpanAndContextScope(newSpan, scope)
          res
        },
        err => {
          finishSpanAndContextScope(newSpan, scope, err)
          err
        }
      )
      catch {
        case e: Throwable =>
          finishSpanAndContextScope(newSpan, scope, e)
          throw e
      }
    }
  }.flatten

  def finishSpanAndContextScope(span: Span, scope: Storage.Scope): Unit = {
    span.finish()
    scope.close()
  }

  def finishSpanAndContextScope(span: Span, scope: Storage.Scope, throwable: Throwable): Unit = {
    span.fail(throwable.getMessage, throwable)
    span.finish()
    scope.close()
  }

  def withNewAsyncSpan[T](operationName: String, spanKind: String, component: String, tags: Map[String, String] = Map.empty)(code: Span ⇒ Future[T]): Future[T] = {
    val spanBuilder = Kamon.spanBuilder(operationName)
      .tagMetrics("span.kind", spanKind)
      .tagMetrics("component", component)
    val spanBuilderWithTags = tags.foldLeft(spanBuilder) { case (s, (k, v)) => s.tag(k, v) }

    val span = spanBuilderWithTags.start()

    withSpanAsync(span)(code(span))

  }

  def withSpanAsync[T](span: Span)(f: => Future[T]): Future[T] = {
    try {
      runWithContextEntry(Span.Key, span)(f).transform(
        s = response => {
          span.finish()
          response
        },
        f = error => {
          span.fail("eror.object", error)
          span.finish()
          error
        })(CallingThreadExecutionContext)
    } catch {
      case t: Throwable =>
        span.fail(t.getMessage, t)
        span.finish()
        throw t

    }
  }

}
