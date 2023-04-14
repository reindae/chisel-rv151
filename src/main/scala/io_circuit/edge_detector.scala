// See README.md for license details.

package edge_detector

import chisel3._
import chisel3.util._

/**
 * A edge detector circuit that detects rising edges.
 * It outputs a 1 cycle wide pulse when its corresponding input transitions from 0 to 1.
 * @param WIDTH -> width of the data input/output
 */

class edge_detector(val WIDTH: Int) extends Module {
  val io = IO(new Bundle {
    /**
     * IO consists of in/out signals
     * The clock has been embedded into Chisel to achieve synchronization 
     */
    val data_in = Input(UInt(WIDTH.W))
    val data_out = Output(Vec(WIDTH, UInt(1.W)))
  })

  // A reg that stores every bit of delayed input signal
  val delay = RegInit(VecInit(Seq.fill(WIDTH)(0.U(1.W))))
  // Check every !delayed signal input & signal input, creating one-clock-cycle pulse
  
  for (i <- 0 until WIDTH) {
    delay(i) := RegNext(io.data_in(i))     // test for 2 cycle wide pulse
    // delay(i) := io.data_in(i)
    when (io.data_in(i) & !delay(i)) {
      io.data_out(i) := 1.U
    }
    .otherwise {
      io.data_out(i) := 0.U
    }
  }

}
