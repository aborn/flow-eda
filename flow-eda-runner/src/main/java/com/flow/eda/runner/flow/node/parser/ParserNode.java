package com.flow.eda.runner.flow.node.parser;

import com.flow.eda.runner.flow.node.AbstractNode;
import com.flow.eda.runner.flow.node.NodeFunction;
import com.flow.eda.runner.flow.node.NodeVerify;
import com.flow.eda.runner.flow.utils.PlaceholderUtil;
import org.bson.Document;

import java.util.Arrays;
import java.util.List;

import static com.flow.eda.runner.flow.node.NodeVerify.isTrue;
import static com.flow.eda.runner.flow.node.NodeVerify.notNull;

/** 解析器节点，用于解析上一个节点的输出参数 */
public class ParserNode extends AbstractNode {
    private List<String> keys;
    private Document payload;

    public ParserNode(Document params) {
        super(params);
    }

    @Override
    public void run(NodeFunction callback) {
        System.out.println("执行解析器节点");
        Document result = PlaceholderUtil.parse(payload, keys);
        result.append("params", getInput());
        result.putAll(output());
        setStatus(Status.FINISHED);
        callback.callback(result);
    }

    @Override
    protected void verify(Document params) {
        try {
            notNull(params);
            // eg: httpResult.result.$0.type, params.a
            String parseKey = params.getString("parseKey");
            notNull(parseKey);
            String[] parseKeys = parseKey.split(",");
            this.keys = Arrays.asList(parseKeys);
            isTrue(!keys.isEmpty());
            this.payload = new Document(params).append("params", getInput());
        } catch (Exception ignore) {
            NodeVerify.throwWithName("parseKey");
        }
    }
}
