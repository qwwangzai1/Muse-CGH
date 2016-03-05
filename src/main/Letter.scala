package main

import utilities.CollectionOp

case class Letter (segs: IndexedSeq[LetterSeg]) {

  lazy val (startX, endX) = Letter.calculateEndXs(segs)

  def getCurves(indices: Seq[Int]) = indices.map(segs)

  def width = endX - startX

  lazy val (mainSegs, secondarySegs) = {
    CollectionOp.firstIndex(segs)(_.isStrokeBreak) match{
      case Some(i) => segs.splitAt(i+1)
      case None => (segs, IndexedSeq())
    }
  }
}

object Letter{
  val empty = create(IndexedSeq())

  val calculationPointsPerUnit = 50

  val minWidth = 0.01

  def calculateEndXs(segs: IndexedSeq[LetterSeg]) = {
    var startX = Double.MaxValue
    var endX = Double.MinValue
    def updateX(x: Double): Unit = {
      if(x<startX) startX = x
      if(x>endX) endX = x
    }
    segs.foreach{s =>
      s.curve.samples(calculationPointsPerUnit).foreach(v => updateX(v.x))
    }
    (startX, endX)
  }

  def create(segs: IndexedSeq[LetterSeg]): Letter = {
    Letter(segs)
  }
}
