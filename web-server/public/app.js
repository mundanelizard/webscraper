import { createApp } from 'https://unpkg.com/vue@3/dist/vue.esm-browser.js';
import { HomeComponent } from '/components/home.vue.js';
import { SearchComponent} from "./components/search.vue.js";
import { BookComponent } from "./components/book.vue.js";


const App = {
    data() {
        return {
            currentPath: window.location.hash
        }
    },
    computed: {
        currentView() {
            if(this.currentPath.indexOf('search') !== -1) {
                return SearchComponent;
            }

            if (this.currentPath.indexOf('book') !== -1) {
                return BookComponent;
            }

            return HomeComponent
        }
    },
    mounted() {
        window.addEventListener('hashchange', () => {
            this.currentPath = window.location.hash
        })
    },
    template: `
    <component :is="currentView" />
    `
}

createApp(App)
    .mount("#app")