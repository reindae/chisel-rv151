// See README.md for license details.

package alu

import chisel3._
import chisel3.util._

/**
 * A synchronizer circuit that convert async signal to sync using 2 FFs, to avoid metastability.
 * This approach allows for an entire clock period for the first flop to resolve metastability.
 * Here, Q2 goes high 1 or 2 cycles later than the input.
 * @param WIDTH -> width of the data input/output
 */

class alu(val WIDTH: Int) extends Module {
  val io = IO(new Bundle {
    /**
     * IO consists of in/out signals
     * The clock has been embedded into Chisel to achieve synchronization 
     */
    val in = Input(UInt(WIDTH.W))
    val out = Output(UInt(WIDTH.W))
  })

  


}
