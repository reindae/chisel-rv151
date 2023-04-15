// See README.md for license details.

package alu

import opcode._
import chisel3._
import chisel3.util._
import chiseltest._
import org.scalatest.freespec.AnyFreeSpec
import chisel3.experimental.BundleLiterals._


class ALUSpec extends AnyFreeSpec with ChiselScalatestTester {
  "alu should compute correct result based on two inputs and one selector" in {
    test(new alu) { dut =>
        dut.io.in_A.poke(7.U)
        dut.io.in_B.poke(5.U)
        dut.io.alu_sel.poke(1.U)
        dut.clock.step(1)
        dut.io.result.expect(12.U)

        dut.io.alu_sel.poke(2.U)
        dut.clock.step(1)
        dut.io.result.expect(5.U)

        dut.io.alu_sel.poke(3.U)
        dut.clock.step(1)
        dut.io.result.expect(7.U)

        dut.io.alu_sel.poke(4.U)
        dut.clock.step(1)
        dut.io.result.expect(2.U)

        dut.io.alu_sel.poke(5.U)
        dut.clock.step(1)
        dut.io.result.expect(2.U)

        dut.io.alu_sel.poke(6.U)
        dut.clock.step(1)
        dut.io.result.expect(0.U)

        dut.io.alu_sel.poke(7.U)
        dut.clock.step(1)
        dut.io.result.expect(224.U)

        dut.io.alu_sel.poke(8.U)
        dut.clock.step(1)
        dut.io.result.expect(0.U)

        dut.io.alu_sel.poke(9.U)
        dut.clock.step(1)
        dut.io.result.expect(0.U)

        dut.io.alu_sel.poke(10.U)
        dut.clock.step(1)
        dut.io.result.expect(0.U)

        dut.io.alu_sel.poke(11.U)
        dut.clock.step(1)
        dut.io.result.expect(5.U)

    }

  }

}
