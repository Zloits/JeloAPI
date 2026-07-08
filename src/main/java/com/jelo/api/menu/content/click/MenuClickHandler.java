package com.jelo.api.menu.content.click;

@FunctionalInterface
public interface MenuClickHandler {

    void handle(MenuClickContext context);
}
