export const withZeroWidthSpace = (markdown: string) =>
    markdown.replace(
        /(?<!^)(?<!\n)([^\s\*\_\~\[\]\(\)\#\>\-\`])/g,
        '$1\u200B'
    );

export const cleanMarkdownUrl = (url: string) =>
    decodeURIComponent(url).replace(/\u200B/g, '');