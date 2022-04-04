package cz.vutfit.umlapp.model;

public class ModelFactory {
    private DataModel dataModel;

    public DataModel getDataModel() {
        if (dataModel == null) {
            dataModel = new DataModel();
        }
        return dataModel;
    }
}
