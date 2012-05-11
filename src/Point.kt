package kool.points1

class Point(val x : Int, val y : Int) {
    fun toString() = "($x, $y)"
}
fun Point.plus(p : Point) = Point(x + p.x, y + p.y)
fun Point.times(factor : Int) = Point(x * factor, y * factor)

