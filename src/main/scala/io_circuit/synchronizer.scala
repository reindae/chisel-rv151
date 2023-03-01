// See README.md for license details.

package synchronizer

import chisel3._
import chisel3.util._

/**
 * A synchronizer circuit that convert async signal to sync using 2 FFs, to avoid metastability.
 * This approach allows for an entire clock period for the first flop to resolve metastability.
 * Here, Q2 goes high 1 or 2 cycles later than the input.
 * @param WIDTH -> width of the data input/output
 */

class synchronizer(val WIDTH: Int) extends Module {
  val io = IO(new Bundle {
    /**
     * IO consists of in/out signals
     * The clock has been embedded into Chisel to achieve synchronization 
     */
    val data_in = Input(UInt(WIDTH.W))
    val data_out = Output(UInt(WIDTH.W))
  })

  val sync_in = Reg(UInt())
  val sync_out = Reg(UInt())

  sync_in := io.data_in
  sync_out := sync_in

  io.data_out := sync_out


}
