package io.github.ivansanchez16.apiresponses;

class MetaView {

    //External View for User
    interface External {
    }
    //Intenal View for User, will inherit all filds in External
    interface Internal extends External {
    }

}
