// See README.md for license details.

package debouncer

import chisel3._
import chisel3.util._

/**
 * A debouncer circuit that removes mechanical bounce and outputs a perfect step function.
 * Simple words, it takes a button's glitchy digital signal and outputs a clean high-to-low signal indicating a single button press
 * @param WIDTH -> width of the data input/output
 * @param SAMPLE_CNT_MAX -> sample counter that count up to SAMPLE_CNT_MAX and output 1
 * @param PULSE_CNT_MAX -> pulse counter that count up to PULSE_CNT_MAX and saturate
 */

class debouncer(val WIDTH: Int = 1, val SAMPLE_CNT_MAX: Int = 65000, val PULSE_CNT_MAX: Int = 200) extends Module {
  val io = IO(new Bundle {
    /**
     * IO consists of in/out signals
     * The clock has been embedded into Chisel to achieve synchronization 
     */
    val glitchy_sigIn = Input(UInt(WIDTH.W))
    val debounced_sigOut = Output(Vec(WIDTH, UInt(1.W)))
  })

  // Attempted using a set of Counter()
  // val sat_cnt = Vec(WIDTH, Counter(PULSE_CNT_MAX + 1))
  // val sat_cnt = Module(new CounterSet(WIDTH, PULSE_CNT_MAX + 1, UInt((log2Ceil(PULSE_CNT_MAX) + 1).W)))
  
  // Sample Pulse Generator
  val SAMPLE_PULSE_gen = Reg(Bool())
  val SAMPLE_cnt = Counter(SAMPLE_CNT_MAX + 1)
  when(SAMPLE_cnt.value < SAMPLE_CNT_MAX.U + 1.U) {
    SAMPLE_cnt.inc()
    SAMPLE_PULSE_gen := SAMPLE_cnt.value === SAMPLE_CNT_MAX.U
  }

  // Debounce logic, width-parameterized
  // Create a saturate counter for each bit of i/o, outputs a 1 for every SAMPLE_CNT_MAX clock cycles, width-parameterized
  for (i <- 0 until WIDTH) {
    val sat_cnt = Counter(PULSE_CNT_MAX + 1)
    when(SAMPLE_PULSE_gen && io.glitchy_sigIn(i) === 1.U && sat_cnt.value < PULSE_CNT_MAX.U) {
      sat_cnt.inc()
    }
    .elsewhen(io.glitchy_sigIn(i) === 0.U) {
      sat_cnt.reset()
    }
    io.debounced_sigOut(i) := sat_cnt.value === PULSE_CNT_MAX.U
  }

}

// A proposed vector set of Counter
// class CounterSet[T <: Data](val numCounters: Int, val maxVal: Int, gen: T) extends Module {
//   val io = IO(new Bundle {
//     val counters = Output(Vec(numCounters, gen))
//   })

//   for (i <- 0 until numCounters) {
//     val cnt = Counter(maxVal)
//     io.counters(i) := cnt.value
//     cnt.inc()
//   }
// }