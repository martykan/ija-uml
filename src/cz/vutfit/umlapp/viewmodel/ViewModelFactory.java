package cz.vutfit.umlapp.viewmodel;

import cz.vutfit.umlapp.model.ModelFactory;

public class ViewModelFactory {
    private final WelcomeViewModel welcomeViewModel;

    public ViewModelFactory(ModelFactory modelFactory) {
        welcomeViewModel = new WelcomeViewModel(modelFactory.getDataModel());
    }

    public WelcomeViewModel getWelcomeViewModel() {
        return welcomeViewModel;
    }
}
