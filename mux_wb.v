module mux_wb(
  input         clock,
  input         reset,
  input  [31:0] io_pc,
  input  [31:0] io_alu_out,
  input  [31:0] io_data_out,
  input  [1:0]  io_wb_sel,
  output [31:0] io_wb
);
  wire [31:0] _io_wb_T_1 = io_pc + 32'h4; // @[mux_nop.scala 23:19]
  wire [31:0] _io_wb_T_3 = 2'h0 == io_wb_sel ? _io_wb_T_1 : 32'h0; // @[Mux.scala 81:58]
  wire [31:0] _io_wb_T_5 = 2'h1 == io_wb_sel ? io_alu_out : _io_wb_T_3; // @[Mux.scala 81:58]
  assign io_wb = 2'h2 == io_wb_sel ? io_data_out : _io_wb_T_5; // @[Mux.scala 81:58]
endmodule
