package com.flow.eda.runner.node.output;

import com.flow.eda.runner.node.AbstractNode;
import com.flow.eda.runner.node.NodeFunction;
import org.bson.Document;

import java.util.Optional;

public class OutputNode extends AbstractNode {
    private final Document output;

    public OutputNode(Document params) {
        super(params);
        this.output = Optional.ofNullable(params).orElseGet(Document::new);
    }

    @Override
    public void run(NodeFunction callback) {
        Document payload = output.append("params", getInput());
        payload.putAll(output());
        setStatus(Status.FINISHED);
        callback.callback(payload);
    }

    @Override
    protected void verify(Document params) {}
}
