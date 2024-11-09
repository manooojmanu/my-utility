package com.utility.interview

class RatingAggregator {

  //  var shows: Set[String] = Set.empty

  type Show = String
  type Viewer = String

  var ratings: Map[Show, Map[Viewer, List[Double]]] = synchronized {
    Map.empty
  }


  def addRating(ratting: Double, show: Show, viewer: Viewer) = {
    val currentRatings = ratings.get(show)
    currentRatings match {
      case Some(availableRatings) =>
        val viewerRatings = availableRatings.get(viewer)
        viewerRatings match {
          case Some(_) => ratings = ratings + (show -> (availableRatings + (viewer -> (viewerRatings.get :+ ratting))))
          case _ => ratings = ratings + (show -> (availableRatings + (viewer -> List(ratting))))
        }
      case _ => ratings = ratings + (show -> Map((viewer -> List(ratting))))
    }
  }


}


