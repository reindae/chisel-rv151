// See README.md for license details.

package synchronizer

import chisel3._
import chiseltest._
import org.scalatest.freespec.AnyFreeSpec
import chisel3.experimental.BundleLiterals._


class SyncSpec extends AnyFreeSpec with ChiselScalatestTester {
  "synchronizer should synchronize input to output" in {

    test(new synchronizer(1)) { dut =>
      // Initialize input and clock signals
      dut.io.data_in.poke(0.U)
      dut.clock.setTimeout(10)

      // Wait for initial reset to complete
      dut.clock.step(1)
    
      // Generate input pulse and wait for two cycles
      dut.io.data_in.poke(1.U)
      dut.clock.step(2)

      // Check that output is synchronized to input
      dut.io.data_out.expect(1.U)

      // Another simple test
      dut.io.data_in.poke(0.U)
      dut.clock.step(2)
      dut.io.data_out.expect(0.U)

    }
  }

}
