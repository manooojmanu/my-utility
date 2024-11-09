
def fib(n: Int): Int = {
  if (n <= 1) n
  else fib(n - 1) + fib(n - 2)
}


def factorial(n: Int): Int = {
  if (n <= 0) 1
  else n * factorial(n - 1)
}