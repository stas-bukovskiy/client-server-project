package edu.clientserver.processor;

import edu.clientserver.message.Message;

public interface Processor {

    void process(Message message);

}
