package com.cw.core.server.tcp;

import com.cw.core.protocol.ProtocolConstant;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.parsetools.RecordParser;

public class TcpBufferHandlerWrapper implements Handler<Buffer> {

    private final RecordParser recordParser;

    public TcpBufferHandlerWrapper(Handler<Buffer> handler) {
        this.recordParser = initRecordParser(handler);
    }

    @Override
    public void handle(Buffer buffer) {
        recordParser.handle(buffer);
    }

    private RecordParser initRecordParser(Handler<Buffer> handler) {

        RecordParser parser = RecordParser.newFixed(ProtocolConstant.MESSAGE_HEADER_LENGTH);

        parser.setOutput(new Handler<Buffer>() {
            //初始化
            int size = -1;
            //一次完整读取(头+体)
            Buffer resultBuffer = Buffer.buffer();

            @Override
            public void handle(Buffer buffer) {
                if (size == -1) {
                    //读取消息体长度
                    size = buffer.getInt(13);
                    parser.fixedSizeMode(size);
                    //写入头信息到结果
                    resultBuffer.appendBuffer(buffer);
                } else {
                    //写入体信息到结果
                    resultBuffer.appendBuffer(buffer);
                    //得到完整的buffer,执行处理
                    handler.handle(resultBuffer);
                    //重置
                    parser.fixedSizeMode(ProtocolConstant.MESSAGE_HEADER_LENGTH);
                    size = -1;
                    resultBuffer = Buffer.buffer();
                }
            }
        });

        return parser;
    }
}
