/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pjs4.gamefactory.displayable.gameobjects;

import pjs4.gamefactory.displayable.GameObject;

/**
 *
 * @author scalpa
 */
public class TestObject extends GameObject {
    
    @Override
    public void innit() {
        String[] components = {"Position", "RigidBody"};
        componentManager.innit(components);
    }
    
    @Override
    public void update() {
        if (componentManager.checkForComponent("Position") && componentManager.checkForComponent("RigidBody")) {
            System.out.println("Ca marche");
        }
    }
    
}
