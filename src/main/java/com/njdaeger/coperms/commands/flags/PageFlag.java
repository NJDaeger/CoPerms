package com.njdaeger.coperms.commands.flags;

import com.njdaeger.bci.base.BCIException;
import com.njdaeger.bci.base.executors.TabExecutor;
import com.njdaeger.bci.defaults.TabContext;
import com.njdaeger.bci.flags.AbstractFlag;
import com.njdaeger.bci.types.defaults.IntegerType;

import java.util.stream.IntStream;

public final class PageFlag extends AbstractFlag<IntegerType> implements TabExecutor<TabContext> {

    public PageFlag() {
        super("p", ':');
    }

    @Override
    public IntegerType getFlagType() {
        return new IntegerType();
    }


    @Override
    public void complete(TabContext context) throws BCIException {
        context.completion(IntStream.rangeClosed(0, 9).mapToObj(String::valueOf).map(s -> context.getCurrent() + s).toArray(String[]::new));
    }
}
