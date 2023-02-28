// See README.md for license details.

package fifo

import chisel3._
import chiseltest._
import org.scalatest.freespec.AnyFreeSpec
import chisel3.experimental.BundleLiterals._


class FifoSpec extends AnyFreeSpec with ChiselScalatestTester {
  "fifo should store and retrieve data in order" in {

    test(new fifo(8, 32)) { dut =>

      // reset fifo
      dut.io.rst.poke(true.B)
      dut.clock.step(1)
      dut.io.empty.expect(true.B)

      // idle state
      dut.io.rst.poke(false.B)
      dut.io.wr.poke(false.B)
      dut.io.rd.poke(false.B)
      dut.clock.step(1)
      dut.io.data_out.expect(0.U)

      // basic write
      dut.io.wr.poke(true.B)
      dut.io.rd.poke(false.B)
      dut.io.data_in.poke(20.U)
      dut.clock.step(1)
      dut.io.data_out.expect(0.U)
      dut.io.full.expect(false.B)
      dut.io.empty.expect(false.B)

      // basic read
      dut.io.wr.poke(false.B)
      dut.io.rd.poke(true.B)
      dut.clock.step(1)
      dut.io.data_out.expect(20.U)
      dut.io.full.expect(false.B)
      dut.io.empty.expect(true.B)

      // continuous write
      dut.io.wr.poke(true.B)
      dut.io.rd.poke(false.B)
      dut.io.data_in.poke(20.U)
      dut.clock.step(1)
      dut.io.data_in.poke(19.U)
      dut.clock.step(1)
      dut.io.data_in.poke(18.U)
      dut.clock.step(1)
      dut.io.full.expect(false.B)
      dut.io.empty.expect(false.B)

      // continuous read
      dut.io.wr.poke(false.B)
      dut.io.rd.poke(true.B)
      dut.clock.step(1)
      dut.io.data_out.expect(20.U)
      dut.clock.step(1)
      dut.io.data_out.expect(19.U)
      dut.clock.step(1)
      dut.io.data_out.expect(18.U)
      dut.clock.step(1)
      dut.io.full.expect(false.B)
      dut.io.empty.expect(true.B)

      // test full
      dut.io.rst.poke(true.B)
      dut.clock.step(1)
      dut.io.rst.poke(false.B)
      dut.io.empty.expect(true.B)
      dut.io.wr.poke(true.B)
      dut.io.rd.poke(false.B)
      for (i <- 0 until 31) {
        dut.io.data_in.poke(i)
        dut.clock.step(1)
      }
      dut.io.full.expect(false.B)
      dut.io.data_in.poke(31)
      dut.clock.step(1)
      dut.io.full.expect(true.B)

      // test empty
      dut.io.wr.poke(false.B)
      dut.io.rd.poke(true.B)
      for (i <- 0 until 31) {
        dut.clock.step(1)
        dut.io.data_out.expect(i)
      }
      dut.io.empty.expect(false.B)
      dut.clock.step(1)
      dut.io.data_out.expect(31)
      dut.io.empty.expect(true.B) 

    }
  }

}
