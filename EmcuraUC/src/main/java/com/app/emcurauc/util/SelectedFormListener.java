package com.app.emcurauc.util;

import com.app.emcurauc.model.Organization;

public interface SelectedFormListener {

    public void selectedForm(Organization organization);
    public void unSelectedForm(Organization organization);
}
