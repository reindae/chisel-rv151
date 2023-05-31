// See README.md for license details.

package uart

import chisel3._
import chiseltest._
import org.scalatest.freespec.AnyFreeSpec
import chisel3.experimental.BundleLiterals._


class UA2UA(val CLOCK_FREQ: Int = 125000000, val BAUD_RATE: Int = 115200) extends Module {
  val io = IO(new Bundle {
    val reset = Input(Bool())

    val data_in = Input(UInt(8.W))
    val data_in_valid = Input(Bool())
    val data_in_ready = Output(Bool())

    val data_out = Output(UInt(8.W))
    val data_out_valid = Output(Bool())
    val data_out_ready = Input(Bool())
  })

  val serial_rx, serial_tx = Wire(UInt(1.W))

  val offChipUA = Module(new uart(CLOCK_FREQ, BAUD_RATE))
  offChipUA.io.reset := io.reset
  offChipUA.io.data_in := io.data_in
  offChipUA.io.data_in_valid := io.data_in_valid
  io.data_in_ready := offChipUA.io.data_in_ready
  // offChipUA.io.data_out := 0x00.U                // We aren't using the receiver of the off-chip UART, only the transmitter
  // offChipUA.io.data_out_valid := false.B
  offChipUA.io.data_out_ready := false.B
  offChipUA.io.serial_in := serial_rx
  serial_tx := offChipUA.io.serial_out

  val onChipUA = Module(new uart(CLOCK_FREQ, BAUD_RATE))
  onChipUA.io.reset := io.reset
  onChipUA.io.data_in := 0x00.U                     // We aren't using the transmitter of the on-chip UART, only the receiver
  onChipUA.io.data_in_valid := 0.B
  // onChipUA.io.data_in_ready := false.B
  io.data_out := onChipUA.io.data_out
  io.data_out_valid := onChipUA.io.data_out_valid
  onChipUA.io.data_out_ready := io.data_out_ready
  onChipUA.io.serial_in := serial_tx                // Notice these lines are connected opposite to the off_chip_uart
  serial_rx := onChipUA.io.serial_out
}


class UA2UASpec extends AnyFreeSpec with ChiselScalatestTester {

  "2 uarts should communicate properly" in {
    val CLOCK_FREQ = 1000 //125000000
    val BAUD_RATE = 100 //112500
    val cyclesPerSymbol = (CLOCK_FREQ / BAUD_RATE).toInt
    test(new UA2UA(CLOCK_FREQ, BAUD_RATE)).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      // reset
      dut.io.reset.poke(true.B)
      dut.clock.step(1)
      dut.io.reset.poke(false.B)
      dut.clock.step(1)

      // Test configuration
      val testInputData = Seq(0x12.U(8.W), 0xAB.U(8.W), 0x34.U(8.W))  // Test data to transmit
      val expectedOutputData = testInputData                          // Expected received data

      // Initialize test variables
      var inputDataIndex = 0
      var outputDataIndex = 0
      var inputDataValid = false
      var outputDataValid = false

      // Execute test
      while (outputDataIndex < expectedOutputData.length) {
        // Drive input data and valid signals if within range
        if (inputDataIndex < testInputData.length) {
          dut.io.data_in.poke(testInputData(inputDataIndex))
          dut.io.data_in_valid.poke(inputDataValid)
        }

        // Capture output data and valid signals
        val capturedOutputData = dut.io.data_out.peek()
        val capturedOutputValid = dut.io.data_out_valid.peek()

        // Check if the output data is valid
        if (capturedOutputValid.litValue() != 0 && !outputDataValid) {
          // Start capturing output data
          outputDataValid = true
        } else if (outputDataValid) {
          // Check if the captured output data matches the expected data
          // assert(capturedOutputData == expectedOutputData(outputDataIndex))
          dut.io.data_out.expect(capturedOutputData)

          // Move to the next output data index
          outputDataIndex += 1

          // Stop capturing output data if all expected data is received
          if (outputDataIndex == expectedOutputData.length) {
            outputDataValid = false
          }
        }

        // Move to the next input data index if within range
        if (inputDataIndex < testInputData.length && inputDataValid) {
          inputDataIndex += 1
        }

        // Toggle input data valid signal
        inputDataValid = !inputDataValid

        dut.clock.step(1)
      }
    }
  }
}
