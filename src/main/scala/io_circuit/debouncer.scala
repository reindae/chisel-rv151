// See README.md for license details.

package debouncer

import chisel3._
import chisel3.util._

/**
 * A debouncer circuit that removes mechanical bounce and outputs a perfect step function.
 * Simple words, it takes a button's glitchy digital signal and outputs a clean high-to-low signal indicating a single button press
 * @param WIDTH -> width of the data input/output
 */

class debouncer(val WIDTH: Int = 1, val SAMPLE_CNT_MAX: Int = 65000, val PULSE_CNT_MAX: Int = 200) extends Module {
  val io = IO(new Bundle {
    /**
     * IO consists of in/out signals
     * The clock has been embedded into Chisel to achieve synchronization 
     */
    val glitchy_sigIn = Input(UInt(WIDTH.W))
    val debounced_sigOut = Output(UInt(WIDTH.W))
  })

  // outputs a 1 for every SAMPLE_CNT_MAX clock cycles.
  val sat_cnt = Counter(PULSE_CNT_MAX + 1)
  
  // Sample Pulse Generator
  val SAMPLE_PULSE_gen = Reg(Bool())
  val SAMPLE_cnt = Counter(SAMPLE_CNT_MAX + 1)
  when(SAMPLE_cnt.value < SAMPLE_CNT_MAX.U + 1.U) {
    SAMPLE_cnt.inc()
    SAMPLE_PULSE_gen := SAMPLE_cnt.value === SAMPLE_CNT_MAX.U
  }

  when(SAMPLE_PULSE_gen && io.glitchy_sigIn === 1.U && sat_cnt.value < PULSE_CNT_MAX.U) {
    sat_cnt.inc()
  }
  .elsewhen(io.glitchy_sigIn === 0.U) {
    sat_cnt.reset()
  }

  // when(sat_cnt.value === PULSE_CNT_MAX && io.glitchy_sigIn === 1.U) {
  //   sat_cnt.
  // }

  io.debounced_sigOut := sat_cnt.value === PULSE_CNT_MAX.U

}
