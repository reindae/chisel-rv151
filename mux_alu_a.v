module mux_alu_a(
  input         clock,
  input         reset,
  input  [31:0] io_rs1,
  input  [31:0] io_pc,
  input  [31:0] io_alu_out,
  input  [31:0] io_mem_out,
  input  [1:0]  io_a_sel,
  output [31:0] io_alu_a
);
  wire [31:0] _io_alu_a_T_1 = 2'h1 == io_a_sel ? io_pc : io_rs1; // @[Mux.scala 81:58]
  wire [31:0] _io_alu_a_T_3 = 2'h2 == io_a_sel ? io_alu_out : _io_alu_a_T_1; // @[Mux.scala 81:58]
  assign io_alu_a = 2'h3 == io_a_sel ? io_mem_out : _io_alu_a_T_3; // @[Mux.scala 81:58]
endmodule
