/*
 * File: ModelFactory.java
 * Authors: Dominik Horký, Tomáš Martykán
 */

package cz.vutfit.umlapp.model;

/**
 * Class for model factory
 */
public class ModelFactory {
    private DataModel dataModel;

    public DataModel getDataModel() {
        if (dataModel == null) {
            dataModel = new DataModel();
        }
        return dataModel;
    }
}
