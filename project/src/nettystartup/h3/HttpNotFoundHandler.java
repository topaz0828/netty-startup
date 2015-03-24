package nettystartup.h3;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import static io.netty.handler.codec.http.HttpHeaders.Names.*;
import static io.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

@ChannelHandler.Sharable
public class HttpNotFoundHandler extends SimpleChannelInboundHandler<HttpRequest> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpRequest req) throws Exception {
        ByteBuf buf = Unpooled.copiedBuffer("Not Found", CharsetUtil.UTF_8);
        FullHttpResponse res = new DefaultFullHttpResponse(HTTP_1_1, NOT_FOUND, buf);
        res.headers().set(CONTENT_TYPE, "text/plain; charset=utf-8");
        if (HttpHeaders.isKeepAlive(req)) {
            res.headers().set(CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
        }
        res.headers().set(CONTENT_LENGTH, buf.readableBytes());
        ctx.writeAndFlush(res).addListener((ChannelFuture f) -> {
            if (!HttpHeaders.isKeepAlive(req)) {
                ctx.channel().close();
            }
        });
    }
}