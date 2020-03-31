package com.njdaeger.coperms.commands.flags;

import com.njdaeger.pdk.command.CommandContext;
import com.njdaeger.pdk.command.TabContext;
import com.njdaeger.pdk.command.exception.ArgumentParseException;
import com.njdaeger.pdk.command.exception.PDKCommandException;
import com.njdaeger.pdk.command.flag.Flag;

import java.util.stream.IntStream;

public final class PageFlag extends Flag<Integer> {

    public PageFlag() {
        super(Integer.class, "Get a page of a text GUI", "-p <page>", "p");
    }


    @Override
    public void complete(TabContext context) {
        context.completion(IntStream.rangeClosed(0, 9).mapToObj(String::valueOf).toArray(String[]::new));
    }

    @Override
    public Integer parse(CommandContext context, String argument) throws PDKCommandException {
        int parsed;
        try {
            parsed = Integer.parseInt(argument);
        } catch (NumberFormatException ignored) {
            throw new ArgumentParseException("Integer argument unable to be parsed. Input: " + argument);
        }
        return parsed;
    }
}
