// See README.md for license details.

package button_parser

import chisel3._
import chisel3.util._
import synchronizer._
import debouncer._
import edge_detector._

/**
 * A button parser circuit that instantiates the synchronizer -> debouncer -> edge detector signal chain for button inputs.
 * @param WIDTH -> width of the data input/output
 * @param SAMPLE_CNT_MAX -> sample counter that count up to SAMPLE_CNT_MAX and output 1
 * @param PULSE_CNT_MAX -> pulse counter that count up to PULSE_CNT_MAX and saturate
 */

class button_parser(val WIDTH: Int = 1, val SAMPLE_CNT_MAX: Int = 25000, val PULSE_CNT_MAX: Int = 150) extends Module {
  val io = IO(new Bundle {
    /**
     * IO consists of in/out signals
     * The clock has been embedded into Chisel to achieve synchronization 
     */
    val data_in = Input(UInt(WIDTH.W))
    val data_out = Output(UInt(WIDTH.W))
  })

  val synchronized_signals = Wire(UInt(WIDTH.W))
  val debounced_signals = Wire(UInt(WIDTH.W))

  val synchronizer = Module(new synchronizer(WIDTH))
  synchronizer.io.data_in := io.data_in
  synchronized_signals := synchronizer.io.data_out

  val debouncer = Module(new debouncer(WIDTH, SAMPLE_CNT_MAX, PULSE_CNT_MAX))
  debouncer.io.glitchy_sigIn := synchronized_signals
  debounced_signals := debouncer.io.debounced_sigOut

  val EdgeDetector = Module(new edge_detector(WIDTH))
  EdgeDetector.io.data_in := debounced_signals
  io.data_out := EdgeDetector.io.data_out

}
