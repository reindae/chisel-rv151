module mux_alu_b(
  input         clock,
  input         reset,
  input  [31:0] io_rs2,
  input  [31:0] io_imm,
  input  [31:0] io_alu_out,
  input  [31:0] io_mem_out,
  input  [1:0]  io_b_sel,
  output [31:0] io_alu_b
);
  wire [31:0] _io_alu_b_T_1 = 2'h1 == io_b_sel ? io_imm : io_rs2; // @[Mux.scala 81:58]
  wire [31:0] _io_alu_b_T_3 = 2'h2 == io_b_sel ? io_alu_out : _io_alu_b_T_1; // @[Mux.scala 81:58]
  assign io_alu_b = 2'h3 == io_b_sel ? io_mem_out : _io_alu_b_T_3; // @[Mux.scala 81:58]
endmodule
