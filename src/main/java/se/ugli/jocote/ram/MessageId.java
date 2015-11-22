package se.ugli.jocote.ram;

import java.util.UUID;

class MessageId {

    final String value;

    MessageId() {
        this.value = UUID.randomUUID().toString();
    }


}
