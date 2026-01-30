export const withZeroWidthSpace = (markdown: string) =>
    markdown.replace(
        /(?<!^)(?<!\n)([^\s\*\_\~\[\]\(\)\#\>\-\`])/g,
        '$1\u200B'
    );