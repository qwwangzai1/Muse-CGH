package main

import java.awt.geom.{Ellipse2D, Line2D}
import java.awt.{BasicStroke, Color, Graphics2D, RenderingHints}

import utilities.Vec2

/**
  * Created by weijiayi on 2/29/16.
  */
class CurveDrawer(val g2d: Graphics2D, pointTransform: Vec2 => Vec2, scaleFactor: Double) {
  g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

  def setColor(color: Color) = g2d.setColor(color)

  def drawCurve(inkCurve: InkCurve): Unit = inkCurve match{
    case InkCurve(curve, dots, start, end) =>
      val dt = 1.0/dots
      val deltaR = (end-start)/dots

      val points = for(i <- 0 until dots) yield {
        val t = i*dt
        val p = curve.eval(t)
        val r = start + i * deltaR
        (p,r)
      }
      for(i <- 0 until dots-1){
        val (p0,_) = points(i)
        val (p1,r1) = points(i+1)
        drawLine(p0,p1,r1)
      }
  }

  def drawCurveControlPoints(inkCurve: InkCurve, endpointColor: Color, controlColor: Color, lineWidth: Double): Unit = inkCurve match{
    case InkCurve(curve, dots, start, end) =>
      setColor(endpointColor)
      drawDot(curve.p0, start)
      drawDot(curve.p3, end)

      val controlR = (start+end)/2
      setColor(controlColor)
      drawDot(curve.p1, controlR)
      drawDot(curve.p2, controlR)
      drawLine(curve.p1,curve.p0,lineWidth)
      drawLine(curve.p2,curve.p3,lineWidth)
  }

  def drawLine(p0: Vec2, p1: Vec2, width: Double): Unit ={
    g2d.setStroke(new BasicStroke((width*scaleFactor).toFloat))
    val line = new Line2D.Double(pointTransform(p0), pointTransform(p1))
    g2d.draw(line)
  }

  def drawDot(center: Vec2, radius: Double): Unit = {
    val c = pointTransform(center)
    val r = radius * scaleFactor
    val dot = new Ellipse2D.Double(c.x-r, c.y-r, 2*r, 2*r)

    g2d.fill(dot)
  }

  def drawLetter(letter: Letter, mainStrokeColor: Color, highlightColor: Color, highlights: Seq[Int]) = {
    letter.segs.zipWithIndex.foreach{case (s, i) =>
      if(!(highlights contains i)){
        setColor(mainStrokeColor)
        drawCurve(s)
      }
    }
    letter.segs.zipWithIndex.foreach{case (s, i) =>
      if(highlights contains i){
        setColor(highlightColor)
        drawCurve(s)
      }
    }
  }
}

object CurveDrawer{
  def colorWithAlpha(c: Color, double: Double): Color = {
    val a = (double * 255).toInt
    new Color(c.getRed, c.getGreen, c.getBlue, a)
  }
}