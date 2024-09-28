package scripts

import am9.olbcore.onebot.script.api.ApiGroupMessageEvent
import am9.olbcore.onebot.script.api.async.AsyncUtil

if (apiEvent instanceof ApiGroupMessageEvent) {
    final def event = (ApiGroupMessageEvent) apiEvent
    if (event.message == "hello") {
        AsyncUtil.async {
            event.reply("hello world")
            Thread.sleep(2000)
            event.reply("hello world")
        }
    }
}
