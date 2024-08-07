const { InputField } = require('./input-field.js');

module.exports.InputFieldObject = class extends InputField {
        constructor() {
            super();
            this.defaultOptions = {
                ...this.defaultOptions,
                representant: '',
                subform: {}
            };
            this.collapsed = false;
        }

        collapseObjectGroupHandler(event) {
            let collapseBtn = event.srcElement;
            let self = collapseBtn.parentElement.parentElement;
            let model = undefined;
            let collapseTitle = ''
            try {
                model = self.getModel();
                collapseTitle = `${self.options.label} ${model ? (self.options.representant ? (model[self.options.representant] ? model[self.options.representant] : '') : Object.values(model)[0]) : ''}`;
            } catch (err) {
                console.log('SourceTree')
            }
            let label = self.querySelector(`label[for="${self.options.name}"]`)
            let collapseEle = self.querySelector(`#${self.options.name}`);
            if (!self.collapsed) {
                collapseEle.classList.add("hidden");
                // collapseEle.style.maxHeight = '0px';
                collapseBtn.innerText = 'ausklappen';
                self.collapsed = !self.collapsed;
                label.innerText = collapseTitle;
            } else {
                collapseEle.classList.remove("hidden");
                // collapseEle.style.maxHeight = '10000px';
                collapseBtn.innerText = 'einklappen';
                self.collapsed = !self.collapsed;
                label.innerText = self.options.label || '';
            }
        }

        applyTemplate() {
                this.rootElement.insertAdjacentHTML('beforeend', `
          <div class="form-element">
              <button class="form-object-collapse" type="button" tabIndex="-1">einklappen</button>
              <label for="${this.options.name}">${this.options.label || ''}</label><br>
              <div id="${this.options.name}" class="form-group">
                  ${Object.keys(this.options.subform).map(key => {
                      let sub = this.options.subform[key];
                      let result = `
                          <${this.mapFieldType(sub.feldtyp)} 
                              ${Object.keys(sub).map(key => `${key}='${this.saveValue(key, sub[key])}'`).join(' ')}
                              ${this.options.initialModel && (this.options.initialModel[sub.name]) ?  `initialModel='${this.saveValue('initialModel', this.options.initialModel[sub.name])}'` : ''} 
                          ></${this.mapFieldType(sub.feldtyp)}>`;
                      return result;
                  }).join('\n')}
              </div>
          </div>
      `);
      this.querySelector('button.form-object-collapse').addEventListener('click', this.collapseObjectGroupHandler);
  }

  getModel(){
      let model = {};
      [...this.querySelector(`#${this.options.name}`).children].forEach(objProps => {
          let partialModel = objProps.getModel();
          if(partialModel)
              model[JSON.parse(objProps.getAttribute('name'))] = partialModel;
      })
      if(Object.keys(model).length === 0)
          return undefined;
      else
          return model;
  }

  checkValidity(){
      let valid = true;
      [...this.querySelector(`#${this.options.name}`).children].forEach(objProps => {
          //TODO CHANGE BACK if not implementing events like this. let partialValidity = objProps.checkValidity();
          let partialValidity = objProps.checkValidity(); 
          // let partialValidity = objProps.valid; 
          if (!partialValidity) console.log(objProps);
          valid = valid && partialValidity;
      })
      return valid;
  }
}