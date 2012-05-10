package demo

import java.lang.Math.*
import javax.swing.JPanel
import java.awt.Graphics
import java.util.Timer
import java.util.TimerTask
import com.sun.tools.javac.resources.legacy
import javax.swing.JFrame
import java.awt.Graphics2D
import java.awt.Component
import java.util.WeakHashMap
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.util.List
import java.util.ArrayList
import java.util.Random
import java.awt.Color
import java.awt.RenderingHints

class Wheel(
        var center : Point,
        var radius : Double,
        var offset : Int,
        var angle : Double = 0.0
    ) {
    val dotCenter : Point
        get() = center + polarPoint(offset.toDouble(), angle)
}

fun wheels() {
    var wheel1 = Wheel(
            center = Point(450, 200),
            radius = 100.0,
            offset = 70
    )
    var wheel2 = Wheel(
            center = Point(200, 200),
            radius = 100.0,
            offset = 70
    )
    val frame = mainFrame("Demo") {
        setSize(1024, 768)

        getContentPane()?.add(drawPanel {
            fun Wheel.draw() {
                drawCircle(center, radius)

                for (i in 1..4) {
                    drawLine((center - Point(radius, 0.0)) to center rotateAroundB (i * 45.deg + angle) ofLength radius * 2 )
                }

                drawCircle(dotCenter, 10.0)
            }

            wheel1.draw()
            wheel2.draw()

            drawLine(wheel2.dotCenter to wheel1.dotCenter)
        })
    }
    val timer = Timer("main")
    timer.repeatEvery(10) {
        val speed = 1.2.deg
        wheel1.angle += speed
        wheel2.angle += speed

        val dx = speed * wheel1.radius
        wheel1.center += Point(dx, 0.0)
        wheel2.center += Point(dx, 0.0)
        frame.repaint()

        if (wheel2.center.x - wheel2.radius > frame.getWidth()) {
//            cancel()
            wheel1.center = Point(-wheel2.radius, 200.0)
            wheel2.center = wheel1.center - Point(250.0, 0.0)
        }
    }
    frame.show()
}

fun arrows() {
    val width = 800
    val height = 600
    val arrows = Array(10) {
        randomSegment(width / 2, height / 2, 100, 150)
    }
    val frame = mainFrame("Arrows") {
        setSize(width, height)
        val panel = drawPanel {
            for (arr in arrows) {
                // Arrows
                                setColor(Color.BLACK)
                                drawLine(arr)
                                val da = 7.deg
                                setColor(Color.RED)
                                val r = 30.0
                                drawLine(arr.b, arr.b + polarPoint(r, arr.angle - da - PI))
                                drawLine(arr.b, arr.b + polarPoint(r, arr.angle + da - PI))

                // Compass circles
//                val r = (arr.abs / 2).i
//                val middle = arr.middle
//                val diam = middle to arr.a ofLength arr.abs
//                setColor(Color.RED)
//                fillArc(diam.middle.x.i - r, diam.middle.y.i - r, r * 2, r * 2, -diam.angle.toDeg(), 180)
//                setColor(Color.BLUE)
//                fillArc(diam.middle.x.i - r, diam.middle.y.i - r, r * 2, r * 2, -diam.angle.toDeg(), -180)
//                setColor(Color.BLACK)
//                fillCircle(middle, 10.0)
//                drawLine(diam)
//                drawCircle(arr.a, r.toDouble())
            }
        }.mouse
                .moved { e ->
            val p = Point(e.getPoint()!!)
            for (i in arrows.indices) {
                val arr = arrows[i]
                arrows[i] = arr ofAngle ((p - arr.a).angle)
            }
            repaint()
        }
                .done
        add(panel)
    }
    frame.show()
}

fun main(args : Array<String>) {
    arrows()
//    wheels()
}


//////////////

class Point(val x : Double, val y : Double)
fun Point(p : java.awt.Point) = Point(p.x, p.y)
fun Point(x : Int, y : Int) = Point(x.toDouble(), y.toDouble())
fun Point.rotate(dAngle : Double) = polarPoint(abs, angle + dAngle)
val Point.abs : Double
    get() = sqrt(sqr(x) + sqr(y))

fun Point.plus(p : Point) = Point(x + p.x, y + p.y)

val Point.norm : Point
    get() = Point(x / abs, y / abs)

fun Point.minus(p : Point) = Point(x - p.x, y - p.y)
fun Point.times(length : Double) = Point(x * length, y * length)
fun Point.div(factor : Double) = Point(x / factor, y / factor)
fun Point.plus(length : Double) = Point(x + length, y + length)
fun Point.minus(length : Double) = Point(x - length, y - length)

fun polarPoint(r : Double, angle : Double) = Point(r * cos(angle), r * sin(angle))

val Point.angle : Double
    get() = atan2(y, x)

val random = Random()
fun randomPoint(maxX : Int, maxY : Int) = Point(random.nextInt(maxX), random.nextInt(maxY))

////////////////////////

class Segment(val a : Point, val b : Point)

fun Point.to(other : Point) = Segment(this, other)

fun Segment.ofLength(length : Double) = Segment(a, a + (b - a).norm * length)

fun Segment.rotateAroundB(angle : Double) = Segment(b + ((a - b) rotate angle), b)

fun randomSegment(maxX : Int, maxY : Int, minR : Int, maxR : Int) : Segment{
    val a = randomPoint(maxX, maxY)
    val ang = random.nextDouble() * 2 * PI
    val r = minR + random.nextDouble() * (maxR - minR)
    return Segment(a, a + polarPoint(r, ang))

}

fun Segment.toPoint() = b - a

val Segment.angle : Double
    get() = toPoint().angle

val Segment.abs : Double
    get() = toPoint().abs

val Segment.middle : Point
    get() = a + toPoint() / 2.0

fun Segment.ofAngle(ang : Double) = Segment(a, a + polarPoint(abs, ang))

///////////////////////

///////////////////////

fun Graphics.drawCircle(center : Point, r : Double) {
    val intR = (r * 2).toInt()
    drawOval((center.x - r).toInt(), (center.y - r).toInt(), intR, intR)
}

fun Graphics.fillCircle(center : Point, r : Double) {
    val intR = (r * 2).toInt()
    fillOval((center.x - r).toInt(), (center.y - r).toInt(), intR, intR)
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

val Double.i : Int
    get() = toInt()

fun Double.toDeg() = (this * 180.0 / PI).i

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
            val g2d = g as Graphics2D
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            g2d.paint()
        }
    }
}


val Component.mouse : Mouse
    get() = Mouse(this)


class MEvent {
    private val handlers : List<(MouseEvent) -> Unit> = ArrayList()

    fun fire(e : MouseEvent) {
        for (handler in handlers) {
            handler(e)
        }
    }

    fun add(handler : (MouseEvent) -> Unit) {
        handlers add handler
    }
}

class Mouse(private val component : Component) {

    private val clicked = MEvent();
    private val moved = MEvent();
    private val released = MEvent();
    private val pressed = MEvent();

    {
        val listener = object : MouseAdapter() {
            public override fun mouseMoved(e : MouseEvent?) {
                moved.fire(e!!)
            }
            public override fun mouseClicked(e : MouseEvent?) {
                clicked.fire(e!!)
            }
            public override fun mouseReleased(e : MouseEvent?) {
                released.fire(e!!)
            }
            public override fun mousePressed(e : MouseEvent?) {
                pressed.fire(e!!)
            }
        }

        component.addMouseListener(listener)
        component.addMouseMotionListener(listener)
        component.addMouseWheelListener(listener)
    }

    fun clicked(body : Component.(MouseEvent) -> Unit) : Mouse {
        clicked add {e -> component.body(e)}
        return this
    }

    fun moved(body : Component.(MouseEvent) -> Unit) : Mouse {
        moved add {e -> component.body(e)}
        return this
    }

    fun released(body : Component.(MouseEvent) -> Unit) : Mouse {
        released add {e -> component.body(e)}
        return this
    }

    fun pressed(body : Component.(MouseEvent) -> Unit) : Mouse {
        pressed add {e -> component.body(e)}
        return this
    }

    val done : Component
        get() = component
}

////

fun Timer.repeatEvery(period : Long, initialDelay : Long = 0, taskBody : TimerTask.() -> Unit) : TimerTask {
    val task = object : TimerTask() {
        public override fun run() {
            taskBody()
        }
    }
    schedule(task, initialDelay, period)
    return task
}
