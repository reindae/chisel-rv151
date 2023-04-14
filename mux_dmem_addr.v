module mux_dmem_addr(
  input         clock,
  input         reset,
  input  [31:0] io_alu_out,
  input  [31:0] io_mem_out,
  input         io_sel,
  output [31:0] io_dmem_addr
);
  assign io_dmem_addr = io_sel ? io_mem_out : io_alu_out; // @[mux_dmem_addr.scala 21:22]
endmodule
