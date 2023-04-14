module IO(
  input         clock,
  input         reset,
  input         io_rst,
  input  [31:0] io_data_addr,
  input  [31:0] io_s3_inst,
  output [31:0] io_cycle_p,
  output [31:0] io_inst_p,
  output [31:0] io_corr_B_p,
  output [31:0] io_total_B_p
);
`ifdef RANDOMIZE_REG_INIT
  reg [31:0] _RAND_0;
  reg [31:0] _RAND_1;
  reg [31:0] _RAND_2;
  reg [31:0] _RAND_3;
`endif // RANDOMIZE_REG_INIT
  reg [31:0] prev_inst; // @[IO.scala 25:22]
  reg [31:0] cycle_p; // @[IO.scala 26:20]
  reg [31:0] inst_p; // @[IO.scala 27:19]
  reg [31:0] total_B_p; // @[IO.scala 29:22]
  wire [31:0] _cycle_p_T_1 = cycle_p + 32'h1; // @[IO.scala 44:24]
  wire [31:0] _inst_p_T_1 = inst_p + 32'h1; // @[IO.scala 47:24]
  wire [31:0] _total_B_p_T_1 = total_B_p + 32'h1; // @[IO.scala 50:30]
  assign io_cycle_p = cycle_p; // @[IO.scala 31:14]
  assign io_inst_p = inst_p; // @[IO.scala 32:13]
  assign io_corr_B_p = 32'h0; // @[IO.scala 33:15]
  assign io_total_B_p = total_B_p; // @[IO.scala 34:16]
  always @(posedge clock) begin
    if (io_rst | io_data_addr == 32'h80000018) begin // @[IO.scala 36:53]
      prev_inst <= 32'h13; // @[IO.scala 41:15]
    end else if (io_s3_inst != 32'h13 & io_s3_inst != prev_inst) begin // @[IO.scala 45:62]
      prev_inst <= io_s3_inst; // @[IO.scala 46:17]
    end
    if (io_rst | io_data_addr == 32'h80000018) begin // @[IO.scala 36:53]
      cycle_p <= 32'h0; // @[IO.scala 37:13]
    end else begin
      cycle_p <= _cycle_p_T_1; // @[IO.scala 44:13]
    end
    if (io_rst | io_data_addr == 32'h80000018) begin // @[IO.scala 36:53]
      inst_p <= 32'h0; // @[IO.scala 38:12]
    end else if (io_s3_inst != 32'h13 & io_s3_inst != prev_inst) begin // @[IO.scala 45:62]
      inst_p <= _inst_p_T_1; // @[IO.scala 47:14]
    end
    if (io_rst | io_data_addr == 32'h80000018) begin // @[IO.scala 36:53]
      total_B_p <= 32'h0; // @[IO.scala 40:15]
    end else if (io_s3_inst[6:0] == 7'h63) begin // @[IO.scala 49:50]
      total_B_p <= _total_B_p_T_1; // @[IO.scala 50:17]
    end
  end
// Register and memory initialization
`ifdef RANDOMIZE_GARBAGE_ASSIGN
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_INVALID_ASSIGN
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_REG_INIT
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_MEM_INIT
`define RANDOMIZE
`endif
`ifndef RANDOM
`define RANDOM $random
`endif
`ifdef RANDOMIZE_MEM_INIT
  integer initvar;
`endif
`ifndef SYNTHESIS
`ifdef FIRRTL_BEFORE_INITIAL
`FIRRTL_BEFORE_INITIAL
`endif
initial begin
  `ifdef RANDOMIZE
    `ifdef INIT_RANDOM
      `INIT_RANDOM
    `endif
    `ifndef VERILATOR
      `ifdef RANDOMIZE_DELAY
        #`RANDOMIZE_DELAY begin end
      `else
        #0.002 begin end
      `endif
    `endif
`ifdef RANDOMIZE_REG_INIT
  _RAND_0 = {1{`RANDOM}};
  prev_inst = _RAND_0[31:0];
  _RAND_1 = {1{`RANDOM}};
  cycle_p = _RAND_1[31:0];
  _RAND_2 = {1{`RANDOM}};
  inst_p = _RAND_2[31:0];
  _RAND_3 = {1{`RANDOM}};
  total_B_p = _RAND_3[31:0];
`endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end // initial
`ifdef FIRRTL_AFTER_INITIAL
`FIRRTL_AFTER_INITIAL
`endif
`endif // SYNTHESIS
endmodule
