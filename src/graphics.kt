package kool.awt.graphics1

import kool.points1.Point
import java.awt.Graphics

fun Graphics.fillCircle(center : Point, radius : Int) {
    fillOval(center.x - radius, center.y - radius, radius * 2, radius * 2)
}
fun Graphics.drawCircle(center : Point, radius : Int) {
    drawOval(center.x - radius, center.y - radius, radius * 2, radius * 2)
}

fun r(it : () -> Unit) : Runnable = object : Runnable {
    public override fun run() {
        it()
    }
}