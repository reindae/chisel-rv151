module mux_rs1(
  input         clock,
  input         reset,
  input  [31:0] io_rs1,
  input  [31:0] io_alu_out,
  input         io_sel,
  output [31:0] io_rs1_out
);
  assign io_rs1_out = io_sel ? io_alu_out : io_rs1; // @[mux_rs1.scala 21:20]
endmodule
