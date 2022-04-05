package cz.vutfit.umlapp.view;

import cz.vutfit.umlapp.model.ModelFactory;

public interface IController {
    void init(ModelFactory modelFactory, ViewHandler viewHandler);
}
