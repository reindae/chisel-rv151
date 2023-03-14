// See README.md for license details.

package dac

import chisel3._
import chisel3.util._

/**
 * A DAC circuit that drives pwm output based on the code input.
 * Assuming clock cycles are 0-indexed, the code is the clock cycle (up to and including) 
 * in the pulse window during which the pwm output should be high.
 * code = 0 is an edge case where pwm should be 0 for the entire pulse window of 1024 cycles.
 * @param CYCLES_PER_WINDOW -> number of cycles in one pulse window
 */

class dac(val CYCLES_PER_WINDOW: Int = 1024) extends Module {
  val io = IO(new Bundle {
    /**
     * IO consists of in/out signals
     * The clock has been embedded into Chisel to achieve synchronization 
     */
    val code_in = Input(UInt(log2Ceil(CYCLES_PER_WINDOW).W))
    val next_sample_out = Output(UInt())
    val pwm_out = Output(UInt())
  })
  
  val cy_cnt = Counter(CYCLES_PER_WINDOW)
  val move = true.B
  when (move) {
    cy_cnt.inc()
  }
  if (cy_cnt.value == CYCLES_PER_WINDOW.U - 1.U) {
    io.next_sample_out := 1.U
  }
  else {
    io.next_sample_out := 0.U
  }
  
  when (cy_cnt.value < io.code_in) {
    io.pwm_out := 1.U
  }
  .otherwise {
    io.pwm_out := 0.U
  }


}
