// See README.md for license details.

package edge_detector

import chisel3._
import chiseltest._
import org.scalatest.freespec.AnyFreeSpec
import chisel3.experimental.BundleLiterals._


class EDSpec extends AnyFreeSpec with ChiselScalatestTester {
  "Edge Detector should detect rising edges and output a one-cycle wide pulse" in {

    // test(new EdgeDetector(1)) { dut =>

    //   // def readExpect(addr: Int, value: Int, port: Int = 0): Unit = {
    //   //   dut.io.data_in(port).poke(addr.U)
    //   //   dut.io.data_out(port).expect(value.U)
    //   // }

    //   // Initialize input and clock signals
    //   dut.io.data_in.poke(0.U)
    //   dut.clock.setTimeout(10)

    //   // Wait for initial reset to complete
    //   dut.clock.step(1)
    
    //   // Generate input pulse and wait for two clock cycles
    //   dut.io.data_in.poke(1.U)
    //   dut.clock.step(2)

    //   // Check that output is synchronized to input
    //   dut.io.data_out(0).expect(0.U)
    //   dut.clock.step(1)

    //   // Generate input pulse and wait for two clock cycles
    //   dut.io.data_in.poke(1.U)
    //   dut.clock.step(2)

    //   // Check that output is only one cycle wide
    //   dut.io.data_out(0).expect(0.U)

    // }

    
    test(new edge_detector(2)) { c =>
      c.io.data_in.poke(1.U)
      c.clock.step(2)
      c.io.data_out(0).expect(0.U)
      c.io.data_out(1).expect(0.U)

      c.io.data_in.poke(2.U)
      c.clock.step(1)
      c.io.data_out(0).expect(0.U)
      c.io.data_out(1).expect(1.U)

      c.clock.step(1)
      c.io.data_out(0).expect(0.U)
      c.io.data_out(1).expect(0.U)      


    }
  }

}
