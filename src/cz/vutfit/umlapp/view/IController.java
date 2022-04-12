/*
 * File: IController.java
 * Authors: Dominik Horký, Tomáš Martykán
 */

package cz.vutfit.umlapp.view;

import cz.vutfit.umlapp.model.ModelFactory;

/**
 * Interface controller for model and view
 */
public interface IController {
    void init(ModelFactory modelFactory, ViewHandler viewHandler);
}
