// See README.md for license details.

package uart

import chisel3._
import chisel3.util._
import uart_receiver._
import uart_transmitter._

/**
 * A uart circuit that combines the receiver and transmitter
 * @param CLOCK_FREQ -> clock frequency
 * @param BAUD_RATE -> baud rate, represents the number of bits per second that can be received
 * SYMBOL_EDGE_TIME: width of a bit in cycles of the system clock -> CLOCK_FREQ / BAUD_RATE
 */

class uart(val CLOCK_FREQ: Int = 125000000, val BAUD_RATE: Int = 115200) extends Module {
  val io = IO(new Bundle {

    val reset = Input(Bool())

    val serial_in = Input(UInt(1.W))    // input into uareceive from beginning device
    val serial_out = Output(UInt(1.W))  // output from uatransmit to target device, final output of uart

    val data_in = Input(UInt(8.W))      // uatransmit's input, connects from uareceive's outputed data
    val in_valid = Input(Bool())        // uatransmit's input, connects from uareceive's valid signal
    val in_ready = Output(Bool())       // uatransmit's output, connects to uareceive to tell ready to get data

    val data_out = Output(UInt(8.W))    // uareceive's output, connects to uatransmit to transfer data
    val out_valid = Output(Bool())      // uareceive's output, connects to uatransmit to tell it has valid data
    val out_ready = Input(Bool())       // uareceive's input, connects from uatransmit's ready signal

  })

  val serial_in_reg, serial_out_reg = Reg(UInt(1.W))    // reg storage for serial data during transmission
  val serial_out_tx = Wire(UInt(1.W))                   // store the serial data output from uatransmit
  io.serial_out := serial_out_reg

  when (io.reset) {
    serial_out_reg := 1.U
    serial_in_reg := 1.U
  }
  .otherwise {
    serial_out_reg := serial_out_tx
    serial_in_reg := io.serial_in
  }

  // Instanitiate uart receiver
  val uareceive = Module(new uart_receiver(CLOCK_FREQ, BAUD_RATE))
  uareceive.io.reset := io.reset
  io.data_out := uareceive.io.data_out
  io.out_valid := uareceive.io.valid
  uareceive.io.ready := io.out_ready
  uareceive.io.serial_in := serial_in_reg

  // Instanitiate uart transmitter
  val uatransmit = Module(new uart_transmitter(CLOCK_FREQ, BAUD_RATE))
  uatransmit.io.reset := io.reset
  uatransmit.io.data_in := io.data_in
  uatransmit.io.valid := io.in_valid
  io.in_ready := uatransmit.io.ready
  serial_out_tx := uatransmit.io.serial_out

}

