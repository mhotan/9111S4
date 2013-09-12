package com.hotan.ninetripleone.supply.model;

public class EndItemComponent extends AccountableComponent {

    public EndItemComponent(String name, String nsn) {
        super(name, nsn);
    }

    public EndItemComponent(String name, String nsn, int qty) {
        super(name, nsn, qty);
    }
}
