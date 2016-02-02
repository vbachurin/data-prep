export const recipeState = {};

export function RecipeStateService() {
    return {
        show: show,
        hide: hide
    };

    function show() {
        recipeState.visible = true;
    }

    function hide() {
        recipeState.visible = false;
    }
}