package ball

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
import kool.awt.graphics.*
import kool.points.*
import java.awt.Font
import java.awt.GradientPaint
import java.awt.Image
import javax.imageio.ImageIO
import java.io.File
import javax.swing.SwingUtilities
import java.awt.RenderingHints

val WIDTH = 800
val HEIGHT = 600

class BouncingBall(val radius : Int, initialCenter : Point, initialVelocity : Point) {
    private var vx : Int = initialVelocity.x
    private var vy : Int = initialVelocity.y

    var bounds : Point = Point(radius * 2, radius * 2)
        set(v) {
            $bounds = v
            advanceTime(0)
        }

    var center : Point = initialCenter
        private set

    fun advanceTime(timeUnits : Int) {
        center += Point(vx, vy) * timeUnits

        center = Point(
                putInRange(center.x, radius, bounds.x - radius) {
                    vx *= -1;
                },
                putInRange(center.y, radius, bounds.y - radius) {
                    vy *= -1;
                }
        )

        for (listener in updateListeners) {
            listener()
        }
    }

    private fun putInRange(n : Int, min : Int, max : Int, onChange : () -> Unit) : Int {
        if (n < min) {
            onChange()
            return min
        }
        if (n > max) {
            onChange()
            return max
        }
        return n
    }

    val updateListeners : List<() -> Unit> = ArrayList()
}

class View(val model : BouncingBall) : JPanel() {
    {
        model.updateListeners add {
            SwingUtilities.invokeLater(r {repaint()})
        }
    }

    public override fun setBounds(x : Int, y : Int, width : Int, height : Int) {
        super<JPanel>.setBounds(x, y, width, height)
        model.bounds = Point(width, height)
    }

    protected override fun paintComponent(g : Graphics?) {
        super<JPanel>.paintComponent(g)

        val g2d = g?.create() as Graphics2D

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);


        g2d.setColor(Color.ORANGE)
        g2d.fillCircle(model.center, model.radius)
        g2d.setColor(Color.BLACK)
        g2d.drawCircle(model.center, model.radius)


    }
}

val timer = Timer()
fun main(args : Array<String>) {
    val frame = JFrame("Ball")
    frame.setSize(WIDTH, HEIGHT)

    val model = BouncingBall(50, Point(50, 50), Point(1, 1))
    frame.add(View(model), BorderLayout.CENTER)
    frame.show()

    timer.schedule(object : TimerTask() {
        public override fun run() {
            model.advanceTime(1)
        }
    }, 0, 3)

}