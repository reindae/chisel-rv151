module mux_pcsel(
  input         clock,
  input         reset,
  input  [31:0] io_pc_4,
  input  [31:0] io_alu_out,
  input         io_pc_sel,
  output [31:0] io_pc_out
);
  assign io_pc_out = io_pc_sel ? io_alu_out : io_pc_4; // @[mux_pcsel.scala 21:19]
endmodule
