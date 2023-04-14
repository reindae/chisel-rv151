module mux_csr(
  input         clock,
  input         reset,
  input  [4:0]  io_rs1,
  input  [31:0] io_rd1,
  input         io_csr_sel,
  output [31:0] io_csr_din
);
  wire [31:0] _io_csr_din_T = {27'h0,io_rs1}; // @[Cat.scala 33:92]
  assign io_csr_din = io_csr_sel ? _io_csr_din_T : io_rd1; // @[mux_csr.scala 21:20]
endmodule
