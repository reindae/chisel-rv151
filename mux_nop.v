module mux_nop(
  input         clock,
  input         reset,
  input  [31:0] io_pc_4,
  input  [31:0] io_alu_out,
  input         io_pc_sel,
  output [31:0] io_inst
);
  assign io_inst = io_pc_sel ? 32'h13 : io_pc_4; // @[mux_nop.scala 21:17]
endmodule
