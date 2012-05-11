package path

import javax.swing.JFrame
import javax.swing.JPanel
import java.awt.Graphics
import java.awt.BorderLayout
import java.util.Timer
import java.util.TimerTask
import java.awt.Color
import java.awt.Graphics2D
import java.util.List
import java.util.ArrayList
import kool.awt.graphics1.*
import kool.points1.*
import java.awt.Font
import java.awt.GradientPaint
import java.awt.Image
import javax.imageio.ImageIO
import java.io.File
import javax.swing.SwingUtilities
import java.awt.RenderingHints
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent

val WIDTH = 800
val HEIGHT = 600

class Path() {
    private val _points = ArrayList<Point>()
    private var v = 1
    private var position = 0

    val currentPoint : Point?
        get() = if (position < _points.size()) _points[position] else null

    val points : java.lang.Iterable<Point>
        get() = _points

    fun addPoint(p : Point) {
        _points.add(p)

        notifyListeners()
    }

    fun notifyListeners() {
        for (l in listeners) {
            l()
        }
    }

    fun clear() {
        _points.clear()
        position = 0
        notifyListeners()
    }

    fun advance() {
        position += v
        if (position >= _points.size()) {
            v *= -1
            position = _points.size() - 1
        }
        if (position < 0) {
            v *= -1
            position = 0
        }
        notifyListeners()
    }

    fun resetPosition() {
        position = 0
    }

    val listeners : List<() -> Unit> = ArrayList()
}

class View(val model : Path) : JPanel() {
    var animation = false
    {
        model.listeners add {
            SwingUtilities.invokeLater(r {repaint()})
        }

        Timer().repeatEvery(10) {
            if (animation) {
                SwingUtilities.invokeLater(r {model.advance()})
            }
        }

        val listener : MouseAdapter = object : MouseAdapter() {
            var drawing = false

            fun add(e : MouseEvent) {
                model.addPoint(e.getPoint()!!.toPoint())
            }

            public override fun mouseDragged(e : MouseEvent?) {
                add(e!!)
            }

            public override fun mousePressed(e : MouseEvent?) {
                model.clear()
                animation = false
            }

            public override fun mouseReleased(e : MouseEvent?) {
                model.resetPosition()
                animation = true
            }
        }
        addMouseListener(listener)
        addMouseMotionListener(listener)
    }

    protected override fun paintComponent(g : Graphics?) {
        super<JPanel>.paintComponent(g)

        val g2d = g?.create() as Graphics2D

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);


        g2d.setColor(Color.BLACK)

        with (model) {
            forEachPair(points) { a, b ->
                g2d.drawLine(a.x, a.y, b.x, b.y)
            }

            val p = model.currentPoint
            if (p != null && animation) {
                g2d.drawCircle(p, 20)
            }
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


fun with(p : Path, body : Path.() -> Unit) = p.body()
fun java.awt.Point.toPoint() : Point = Point(getX().toInt(), getY().toInt())

fun <T> forEachPair(list : java.lang.Iterable<out T>, body : (T, T) -> Unit) {
    val i = list.iterator()
    if (!i.hasNext()) return
    var a = i.next()
    while (i.hasNext()) {
        val b = i.next()
        body(a, b)
        a = b
    }
}

fun main(args : Array<String>) {
    val frame = JFrame("Ball")
    frame.setSize(WIDTH, HEIGHT)

    val model = Path()
    frame.add(View(model), BorderLayout.CENTER)
    frame.show()
}