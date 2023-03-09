// See README.md for license details.

package debouncer

import chisel3._
import chisel3.util._
import chiseltest._
import org.scalatest.freespec.AnyFreeSpec
import chisel3.experimental.BundleLiterals._


class DebouncerSpec extends AnyFreeSpec with ChiselScalatestTester {
  "debouncer should smooth out glichy signal inputs to debounced output" in {

    test(new debouncer(1, 10, 4)) { dut =>

      // Initially act glitchy
      for (_ <- 0 until 5) {
        dut.io.glitchy_sigIn.poke(0.U)
        dut.clock.step(1)
        dut.io.glitchy_sigIn.poke(1.U)
        dut.clock.step(1)
      }

      // Drop signal for a full sample period
      dut.io.glitchy_sigIn.poke(0.U)
      dut.clock.step(dut.SAMPLE_CNT_MAX + 1)
      dut.clock.step(1)

      // Bring the signal high and hold until before the saturating counter should saturate, then pull low
      dut.io.glitchy_sigIn.poke(1.U)
      dut.clock.step(dut.SAMPLE_CNT_MAX * (dut.PULSE_CNT_MAX - 1))
      dut.clock.step(1)

      // Pull the signal low and wait, the debouncer shouldn't set its output high
      dut.io.glitchy_sigIn.poke(0.U)
      dut.clock.step(dut.SAMPLE_CNT_MAX * (dut.PULSE_CNT_MAX + 1))
      dut.clock.step(1)
      dut.io.debounced_sigOut.expect(0.U, "1st debounced_signal didn't stay low")

      // Second signal
      for (_ <- 0 until 5) {
        dut.io.glitchy_sigIn.poke(0.U)
        dut.clock.step(1)
        dut.io.glitchy_sigIn.poke(1.U)
        dut.clock.step(1)
      }

      // Bring the glitchy signal high and hold past the point at which the debouncer should saturate
      dut.io.glitchy_sigIn.poke(1.U)
      dut.clock.step(dut.SAMPLE_CNT_MAX * (dut.PULSE_CNT_MAX + 1))
      dut.clock.step(1)
      dut.io.debounced_sigOut.expect(1.U)
      dut.clock.step(1)

      // While the glitchy signal is high, the debounced output should remain high
      for (_ <- 0 until 3) {
        dut.io.debounced_sigOut.expect(1.U)
        dut.clock.step(1)
      }

      // Pull the glitchy signal low and the output should fall after the next sampling period
      // The output is only guaranteed to fall after the next sampling period
      // Wait until another sampling period has definetely occured
      dut.io.glitchy_sigIn.poke(0.U)
      dut.clock.step(dut.SAMPLE_CNT_MAX + 1)
      dut.clock.step(1)
      dut.io.debounced_sigOut.expect(0.U)
      dut.clock.step(1)

      // Wait for some time to ensure the signal stays low
      for (_ <- 0 until (dut.SAMPLE_CNT_MAX * (dut.PULSE_CNT_MAX + 1))) {
        dut.io.debounced_sigOut.expect(0.U)
        dut.clock.step(1)
      }

    }
  }

}
