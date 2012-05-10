package ball.no.model

import javax.swing.JFrame
import javax.swing.JPanel
import java.awt.Graphics
import java.awt.BorderLayout
import java.util.Timer
import java.util.TimerTask
import java.awt.Color
import java.awt.Graphics2D

fun main(args : Array<String>) {
    val frame = JFrame("Ball")
    val width = 800
    val height = 600
    frame.setSize(width, height)

    var x = 0
    var y = 0
    val diameter = 100

    frame.add(object : JPanel() {
        protected override fun paintComponent(g : Graphics?) {
            super<JPanel>.paintComponent(g)
            val g2d = g as Graphics2D
            g2d.setColor(Color.BLUE)
            g2d.fillOval(x, y, diameter, diameter)
        }
    }, BorderLayout.CENTER)

    var vx = 5
    var vy = 5
    Timer().schedule(object : TimerTask() {
        public override fun run() {
            x += vx
            y += vy
            if (x > width - diameter || x < 0) {
                vx = -vx
            }
            if (y > height - diameter || y < 0) {
                vy = -vy
            }
            frame.repaint()
        }
    }, 0, 10)

    frame.show()
}