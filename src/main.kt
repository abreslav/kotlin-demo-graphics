package demo

import java.lang.Math.*
import javax.swing.JPanel
import java.awt.Graphics
import java.util.Timer
import java.util.TimerTask
import com.sun.tools.javac.resources.legacy
import javax.swing.JFrame
import java.awt.Graphics2D

class Wheel(
        var center : Point,
        var radius : Int,
        var offset : Int,
        var angle : Double = 0.0
    ) {
    val dotCenter : Point
        get() = center + polarPoint(offset.toDouble(), angle)
}

fun main(args : Array<String>) {
    var wheel1 = Wheel(
            center = Point(450, 200),
            radius = 100,
            offset = 70
    )
    var wheel2 = Wheel(
            center = Point(200, 200),
            radius = 100,
            offset = 70
    )
    val frame = mainFrame("Demo") {
        setSize(1024, 768)

        getContentPane()?.add(drawPanel {
            fun Wheel.draw() {
                drawCircle(center, radius)

                for (i in 1..4) {
                    drawLine((center - Point(radius, 0)) to center rotateAroundB (i * 45.deg + angle) ofLength radius * 2 )
                }

                drawCircle(dotCenter, 10)
            }

            wheel1.draw()
            wheel2.draw()

            drawLine(wheel2.dotCenter to wheel1.dotCenter)
        })
    }
    val timer = Timer("main")
    timer.repeatEvery(10) {
        val speed = 1.2.deg
        wheel1.angle -= speed
        wheel2.angle -= speed

        val dx = speed * wheel1.radius
        wheel1.center += Point(dx, 0.0)
        wheel2.center += Point(dx, 0.0)
        frame.repaint()

        if (wheel2.center.x - wheel2.radius > frame.getWidth()) cancel()
    }
    frame.show()
}


//////////////

class Point(val x : Double, val y : Double)
fun Point(x : Int, y : Int) = Point(x.toDouble(), y.toDouble())
fun Point.rotate(dAngle : Double) = polarPoint(abs, angle + dAngle)
val Point.abs : Double
    get() = sqrt(sqr(x) + sqr(y))

fun Point.plus(p : Point) = Point(x + p.x, y + p.y)

val Point.norm : Point
    get() = Point(x / abs, y / abs)

fun Point.minus(p : Point) = Point(x - p.x, y - p.y)
fun Point.times(length : Int) = Point(x * length, y * length)
fun Point.plus(length : Int) = Point(x + length, y + length)
fun Point.minus(length : Int) = Point(x - length, y - length)

fun polarPoint(r : Double, angle : Double) = Point(r * cos(angle), -r * sin(angle))

val Point.angle : Double
    get() = atan2(y, x)


////////////////////////

class Segment(val a : Point, val b : Point)

fun Point.to(other : Point) = Segment(this, other)

fun Segment.ofLength(length : Int) = Segment(a, a + (b - a).norm * length)

fun Segment.rotateAroundB(angle : Double) = Segment(b + ((a - b) rotate angle), b)

///////////////////////

fun Graphics.drawCircle(center : demo.Point, r : Int) {
    drawOval(center.x.toInt() - r, center.y.toInt() - r, r * 2, r * 2)
}

fun Graphics.drawLine(a : Point, b : Point) {
    drawLine(a.x.toInt(), a.y.toInt(), b.x.toInt(), b.y.toInt())
}

fun Graphics.drawLine(s : Segment) {
    drawLine(s.a, s.b)
}

/////////////

fun sqr(d : Double) = d * d
val Double.deg : Double
    get() = this * PI / 180.0

val Int.deg : Double
    get() = this.toDouble().deg


/////////////////

fun mainFrame(title : String, init : JFrame.() -> Unit) : JFrame {
    val frame = JFrame(title)
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
    frame.init()
    return frame
}

fun drawPanel(paint : Graphics2D.() -> Unit) : JPanel {
    return object : JPanel() {

        protected override fun paintComponent(g : Graphics?) {
            super<JPanel>.paintComponent(g)
            (g as Graphics2D).paint()
        }
    }
}

fun Timer.repeatEvery(period : Long, initialDelay : Long = 0, taskBody : TimerTask.() -> Unit) : TimerTask {
    val task = object : TimerTask() {
        public override fun run() {
            taskBody()
        }
    }
    schedule(task, initialDelay, period)
    return task
}
